// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.server.group;

import com.google.common.base.Strings;
import com.google.gerrit.common.data.GroupDescriptions;
import com.google.gerrit.common.errors.NoSuchGroupException;
import com.google.gerrit.extensions.restapi.DefaultInput;
import com.google.gerrit.extensions.restapi.MethodNotAllowedException;
import com.google.gerrit.extensions.restapi.ResourceNotFoundException;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.reviewdb.client.AccountGroup;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.account.GroupCache;
import com.google.gerrit.server.auth.AuthException;
import com.google.gerrit.server.group.PutDescription.Input;
import com.google.gwtorm.server.OrmException;
import com.google.inject.Inject;

import java.util.Collections;

public class PutDescription implements RestModifyView<GroupResource, Input> {
  static class Input {
    @DefaultInput
    String description;
  }

  private final GroupCache groupCache;
  private final ReviewDb db;

  @Inject
  PutDescription(GroupCache groupCache, ReviewDb db) {
    this.groupCache = groupCache;
    this.db = db;
  }

  @Override
  public Response<String> apply(GroupResource resource, Input input)
      throws MethodNotAllowedException, AuthException, NoSuchGroupException,
      ResourceNotFoundException, OrmException {
    boolean delete = false;
    if (input == null) {
      input = new Input(); // Delete would set description to null.
      delete = true;
    }

    if (!resource.isInternal()) {
      throw new MethodNotAllowedException();
    } else if (!resource.getControl().isOwner()) {
      throw new AuthException("Not group owner");
    }

    AccountGroup group = db.accountGroups().get(
      GroupDescriptions.toAccountGroup(resource.getGroup()).getId());
    if (group == null) {
      throw new ResourceNotFoundException();
    }

    group.setDescription(Strings.emptyToNull(input.description));
    db.accountGroups().update(Collections.singleton(group));
    groupCache.evict(group);

    if (delete) {
      return Response.none();
    }
    return Response.ok(Strings.nullToEmpty(input.description));
  }
}
